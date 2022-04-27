const env = require('@cloudogu/dogu-integration-test-library/lib/environment_variables')

// Loads all steps from the dogu integration library into this project
const doguTestLibrary = require('@cloudogu/dogu-integration-test-library')
doguTestLibrary.registerSteps()