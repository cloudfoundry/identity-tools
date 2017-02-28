#!/bin/bash
#Usage - . ./create_uaa_users.sh <Starting user count> <Number of users to be created>
if [[ $# -lt 2 ]]
then
  echo " Please provide the number of users to be created.. Exiting script"
  exit
fi

usercount=$1
while [ $usercount -lt `expr $2 + 1` ]
do
  echo "Target UAAC to the perf UAA app"
  uaac target https://perfzone$usercount.uaa.ge.com
  echo "Get a token from the admin client to create client, groups and users"
  uaac token client get zoneclient$usercount -s clientseecret
  echo "Update client scope and grant_types"
  uaac client update zoneclient$usercount --scope uaa.resource,scim.read,acs.attributes.read,acs.attributes.write,acs.policies.write,acs.policies.read --authorized_grant_types client_credentials,password,authorization_code --autoapprove true
  echo "Add user" $usercount
  uaac user add zoneuser$usercount --given_name Perf$usercount"FN" --family_name Perf$usercount"LN" --emails zoneuser$usercount@testcf.com -p pass123
  uaac group add acs.attributes.read
  uaac group add acs.attributes.write
  uaac group add acs.policies.write
  uaac group add acs.policies.read
  uaac member add acs.attributes.read zoneuser$usercount
  uaac member add acs.attributes.write zoneuser$usercount
  uaac member add acs.policies.write zoneuser$usercount
  uaac member add acs.policies.read zoneuser$usercount
  usercount=`expr $usercount + 1`
  rm ~/.uaac.yml
done
