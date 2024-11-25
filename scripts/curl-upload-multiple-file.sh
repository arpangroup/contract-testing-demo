## Example with Dynamic File Paths (Loop)
## Ensure the Server Can Handle Multiple Files
file_paths=("/path/to/file1.json" "/path/to/file2.json" "/path/to/file3.json")

curl_command="curl -X POST -H \"Authorization: Bearer $ida_token\""

for file in "${file_paths[@]}"; do
  curl_command+=" -F \"file=@$file\""
done

curl_command+=" https://example.com/upload"

# Execute the constructed command
eval "$curl_command"
