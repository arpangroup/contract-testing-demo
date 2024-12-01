check_var() {
  var_name=$1
  if [ -z "${!var_name}" ]; then
    echo "Error: Environment variable '$var_name' is not set or empty."
    exit 1
  fi
}