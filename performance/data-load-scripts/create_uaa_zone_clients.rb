require 'json'

#Usage - ruby create_uaa_zone_client.rb <Number of Identity zones to be created>
host='http://localhost:8080/uaa'
admin_client_secret='adminsecret'
admin_client_id='admin'

if ARGV.length < 1
  puts ' Please provide the number of Identity zones to be created.. Exiting script'
  exit
end

number_of_zones = ARGV[0]

`uaac target #{host}`
`uaac token client get #{admin_client_id} -s #{admin_client_secret}`

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
    base_command = "uaac curl -X POST -H \"Accept:application/json\" -H \"Content-Type:application/json\" /identity-zones -d '#{to_json()}'"
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

  def checkTokenScope
    zone_admin_authority = "zones.#{@identity_zone}.admin"
    scopes = `uaac token decode | grep "scope:"`
    if !scopes.split(' ').include?(zone_admin_authority)
      raise "Your current token does not have #{zone_admin_authority} in scopes list"
    end
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
    checkTokenScope()
    base_command = "uaac curl -XPOST -H \"Accept: application/json\" -H \"Content-Type: application/json\" -H \"X-Identity-Zone-Id: #{@identity_zone}\" /oauth/clients -d '#{to_json()}'"

    if @skip_ssl
      base_command += ' --insecure'
    end

    `#{base_command}`
  end
end

(1..number_of_zones.to_i).each do |zone_number|
  zone_name = "perfzone#{zone_number}"

  ### Setup UAA Zone and admin client ####
  zone = IdentityZone.new(zone_name, zone_name, "Performance test zone #{zone_number}", "Performance zone")
                     .create()
  `uaac client update #{admin_client_id} --authorities clients.read,zones.read,clients.secret,zones.write,clients.write,clients.admin,uaa.admin,scim.write,scim.read,zones.#{zone_name}.admin`
  `uaac token client get #{admin_client_id} -s #{admin_client_secret}`
  # `./create-zone-admin-client.sh -z #{zone_name} -c zoneclient#{zone_number} -s clientsecret`
  ZoneClient.new("zoneclient#{zone_number}", 'clientsecret', zone.id).create()
end
