#!/bin/bash
source ./scripts/pactflow.config
source ./scripts/common.sh
########################################
## Do not modify script below this line!
########################################

check_var "spec_file_path"
check_var "provider_version"
check_var "provider_name"
check_participant_name "provider_name"
check_var "provider_branch"

# TODO: just use file extension for format
format="yml"
if [ ! -f "$spec_file_path" ]
then
  echo "openapi yml or json file cannot be found"
  exit 3
fi

## Variables to be filled by the user
username=$(whoami)

# user must set env variable on localhost, like:
# > export pass=(user password)
password=$pass
if [ "$password" = "" ]
then
  echo "Password is not specified"
  exit 1
fi

## Generate IDA token
ida_token=$(curl -s -k https://google.com/oauth2/token -d "grant_type=password&client_id=PC-123&username=johndoe&password=$password&resource=demo" | jq ".access_token" | sed 's/"//g"')
if [ "ida_token" = "null" ]
then
  echo "IDA token is null, you probably entered your password wrong, please retry"
  exit 1
fi


## Rest call to create branch
echo "Rest call to create branch. Output:"
curl_out=$(culr -s -X POST -H "Authorization: Bearer $ida_token" -H "Content-Type: application/json" --http1.1 -d "" https://arpangroup.pactflow.io/pacticipants/$provider_name/branches/$(printf  | jq -sRr '@uri')/versions/$provider_version)
curl_result=$(echo $curl_out | jq '.createdAt')
if [ "curl_result" = "null" ]
then
  echo "Failed to create branch. Curl output: $curl_out"
  exit 1
else
  echo "Branch: $provider_branch successfully x=created for provider: $provider_name"
fi
echo ""


## Generate temporary file containing body of the request
oas_base64=$(base64 -w 0 spec_file_path)

printf '{"content":"'${oas_base64}'","contentType":"application/'$format'","verificationResults":{"success":true,"content":"TWFUDshggbzxcgjsbfWQ=","contentType":"text/plain","verifier":"manual"}}' > body.json

## Rest call to upload provider spec
echo "est call to upload provider spec. Output:"
echo -n "Provider spec '$provider_name' uploaded at "
curl_out=$(culr -s -X POST -H "Authorization: Bearer $ida_token" -H "Content-Type: application/json" --http1.1 -d @body.json https://arpangroup.pactflow.io/contracts/provider/$provider_name/version/$provider_version)
curl_result=$(echo $curl_out | jq '.createdAt')

if [ "curl_result" = "null" ]
then
  echo "Failed to publish. Curl Output: $curl_out"
else
  echo "Provider: $provider_name, version: $provider_version successfully published"
fi

## Delete temp file
rm -rf body.json