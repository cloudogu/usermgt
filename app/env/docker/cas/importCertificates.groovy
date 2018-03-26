#!/usr/bin/env groovy
if (args.length < 3){
  println "usage importCertificates keystore storepass hostname";
  System.exit(1);
}

String keystore = args[0];
String storepass = args[1];
String hostname = args[2];

int port = 443;
String openssl = "openssl s_client -showcerts -connect ${hostname}:${port}";

def files = [];

int counter = 0;
StringBuilder content = null;
openssl.execute().in.eachLine { line ->
  if ( line.contains("-BEGIN CERTIFICATE-") ){
    content = new StringBuilder();
  }
  if (content != null){
    content.append(line + System.getProperty("line.separator"));
  }
  if (line.contains("-END CERTIFICATE-")){
    File cert = new File(hostname + "." + (counter++) + ".cert");
    cert.setText(content.toString())
    files.add(cert);
    content = null;
  }
};

for (File f : files){
  String cmd = "keytool -import -noprompt -trustcacerts \
    -alias ${f.name} -file ${f} \
    -keystore ${keystore} -storepass ${storepass}"

  println cmd;
  cmd.execute().waitFor();
}
