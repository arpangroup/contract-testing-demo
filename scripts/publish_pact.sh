#!/bin/bash
echo "loading configs & env variables..........."
pact_file_path="05-kafka-json-consumer/target/pacts/kafka-notification-consumer-kafka-notification-provider.json"
provider_name="kafka-notification-provider"
consumer_name="kafka-notification-consumer"
consumer_version="consumer-version-1"
ida_token="pkqBnpXX3u4o5wErioDeXA"
echo "loaded successfully........................"


# Get the parent directory of the current directory
parent_directory=$(dirname "$PWD")
echo "PARENT_DIRECTORY: $parent_directory"
pact_file_path=$parent_directory/$pact_file_path
echo "PACT_FILE_PATH: ${pact_file_path}"

# Check if the provided directory exists
#if [ ! -d "$directory" ]; then
#  echo "Error: Directory '$directory' does not exist."
#  exit 1
#fi
#
## Print all files in the directory
#echo "Files in directory '$directory':"
#find "$directory" -type f -print





## Rest call to publish consumer spec [-w (write-out); -silent --output to separate the response body and the status code]
echo "Rest call to publish consumer pact. Output: "
response=$(curl -s -o /tmp/curl_response_body -w "%{http_code}" -X PUT \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $ida_token" \
     --data-binary @$pact_file_path \
     https://arpangroup.pactflow.io/pacts/provider/$provider_name/consumer/$consumer_name/version/$consumer_version)

## Read the HTTP response body
response_body=$(cat /tmp/curl_response_body)


# Check HTTP status code
if [ "$response" -eq 200 ]
then
  echo -e "Successfully published"
  echo "Consumer: ${consumer_name}, version: ${consumer_version} successfully publish for provider: $provider_name}"
else
  echo "Failed with status: $response"
  echo "Failed to publish. Curl Output: $response_body"
fi
