#!/bin/bash
source ./scripts/pactflow.config
source ./scripts/common.sh
########################################
# Run from root folder
# Set env variable on localhost, like:
# > export pass=(user password)
########################################

########################################
## Do not modify script below this line!
########################################

## Variables to be filled by the user
username=$(whoami)

check_var "pact_file_path"
pact_file_path=$pact_file_path$(ls -tp ./$pact_file_path | grep -v /$ | head -1)

# TODO: publish _all_ files under **/target/pacts
if [ "$pact_file_path" = "http/target/pacts/" ]
then
  echo "Found no pact files under http/target/pacts/. Please run tests first."
  exit 2
fi

# user must set env variable on localhost, like:
# > export pass=(user password)
password=$pass
if [ "$password" = "" ]
then
  echo "Password is not specified"
  exit 1
fi

check_var "consumer_name"
check_participant_name "consumer_name"
check_var "consumer_version"
check_var "provider_name"
check_participant_name "provider_name"
check_var "consumer_branch"

## Generate IDA token
ida_token=$(curl -s -k https://google.com/oauth2/token -d "grant_type=password&client_id=PC-123&username=johndoe&password=$password&resource=demo" | jq ".access_token" | sed 's/"//g"')
if [ "ida_token" = "null" ]
then
  echo "IDA token is null, you probably entered your password wrong, please retry"
  exit 1
fi

## Generate temporary file containing body of the request
pact_base64=$(base64 -w 0 $pact_file_path)

printf '{"participantName":"'${consumer_name}'","participantVersionNumber":"'${consumer_version}'","tags":[],"branch":"'${consumer_branch}'","contracts":[{"consumerName":"'${consumer_name}'","providerName":"'${provider_name}'","specification":"pact","contentType":"application/json","content":"'${pact_base64}'"}]}' > body.json

## Rest call to publish consumer spec
echo "Rest call to publish consumer pact. Output: "
curl_out=$(culr -s -X POST -H "Authorization: Bearer $ida_token" -H "Content-Type: application/json" --http1.1 -d @body.json https://arpangroup.pactflow.io/contracts/publish)
curl_result=$(echo $curl_out | jq '.notices[1].type')

if [ "curl_result" = "\"success\"" ]
then
  echo "Consumer: ${consumer_name}, version: ${consumer_version} successfully publish for provider: $provider_name}"
else
  echo "Failed to publish. Curl Output: ${curl_out}"
fi

## Delete temp file
rm -rf body.json

