require 'json'

if ARGV.length < 3
  puts 'Usage: create_uaa_zone_clients NUMBER_OF_ZONES NUMBER_OF_CLIENTS_PER_ZONE NUMBER_OF_USERS_PER_ZONE'
  exit
end

number_of_zones = ARGV[0].to_i
number_of_clients_per_zone = ARGV[1].to_i
number_of_users_per_zone = ARGV[2].to_i

if number_of_zones <= 0
  puts 'Must specify a positive number of identity zones'
  exit
end

if number_of_clients_per_zone <= 0
  puts 'Must specify a positive number of clients per zone'
  exit
end

if number_of_users_per_zone <= 0
  puts 'Must specify a positive number of users per zone'
  exit
end

class IdentityZone
  attr_reader :id, :subdomain, :name, :description, :skip_ssl

  def initialize(id, subdomain, name, description, skip_ssl=true)
    @id = id
    @subdomain = subdomain
    @name = name
    @description = description
    @skip_ssl = skip_ssl
  end

  def to_json
    {'id' => @id, 'subdomain' => @subdomain, 'name' => @name, 'description' => @description}.to_json
  end

  def create
    puts "Creating zone #{@id} at #{@subdomain}"
    base_command = "uaac curl -X POST -H \"Accept:application/json\" -H \"Content-Type:application/json\" /identity-zones -d '#{to_json}'"
    if @skip_ssl
      base_command += ' --insecure'
    end

    `#{base_command}`
    self
  end
end

class ZoneClient
  attr_reader :identity_zone, :id, :secret, :skip_ssl

  def initialize(id, secret, identity_zone, skip_ssl=true)
    @id = id
    @secret = secret
    @identity_zone = identity_zone
    @skip_ssl = skip_ssl
  end

  def to_json
    {'client_id' => @id,
     'client_secret' => @secret,
     'authorized_grant_types' => ['client_credentials'],
     'scopes' => ['acs.attributes.read', 'acs.attributes.write', 'acs.policies.write', 'acs.policies.read', 'uaa.resource', 'scim.read'],
     'authorities' => ['clients.admin', 'clients.read', 'clients.write', 'clients.secret', "zones.#{@identity_zone}.admin", 'scim.read', 'scim.write', 'idps.read', 'idps.write', 'uaa.resource'],
     'resource_ids' => ['none'],
     'allowedproviders' => ['uaa']
    }.to_json
  end

  def create
    puts "(In zone #{identity_zone}) Creating client #{@id}"
    base_command = "uaac curl -XPOST -H \"Accept: application/json\" -H \"Content-Type: application/json\" -H \"X-Identity-Zone-Id: #{@identity_zone}\" /oauth/clients -d '#{to_json}'"

    if @skip_ssl
      base_command += ' --insecure'
    end

    `#{base_command}`
    self
  end
end

class ZoneUser
  attr_reader :identity_zone, :username, :password, :skip_ssl

  def initialize(username, password, identity_zone, skip_ssl=true)
    @username = username
    @password = password
    @identity_zone = identity_zone
    @skip_ssl = skip_ssl
  end

  def to_json
    {
        'userName' => @username,
        'name' => {
            'formatted' => 'given name family name',
            'familyName' => "#{@username}FN",
            'givenName' => "#{@username}LN"
        },
        'emails' => [{
                         'value' => "#{@username}@testcf.com",
                         'primary' => true
                     }],
        'password' => @password
    }.to_json
  end

  def create
    puts "(In zone #{identity_zone}) Creating user #{@username}"
    base_command = "uaac curl -XPOST -H \"Accept: application/json\" -H \"Content-Type: application/json\" -H \"X-Identity-Zone-Id: #{@identity_zone}\" /Users -d '#{to_json}'"
    `#{add_ssl_option(base_command)}`
    self
  end

  private

  def add_ssl_option(base_command)
    if @skip_ssl
      base_command += ' --insecure'
    end
    base_command
  end
end

(1..number_of_zones.to_i).each do |zone_number|
  zone_name = "perfzone#{zone_number}"

  ### Setup UAA Zone and admin client ####
  zone = IdentityZone.new(zone_name, zone_name, "Performance test zone #{zone_number}", "Performance zone").create
  number_of_clients_per_zone.times do |client_number|
    ZoneClient.new("client#{client_number}", 'clientsecret', zone.id).create
  end

  number_of_users_per_zone.times do |user_number|
    ZoneUser.new("user#{user_number}", 'password', zone.id).create
  end
end
