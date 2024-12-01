

Publish the Verification result:
````xml
<plugin>
  <groupId>au.com.dius.pact.consumer</groupId>
  <artifactId>pact-jvm-provider-maven-plugin</artifactId>
  <version>4.6.5</version> <!-- Use the latest version -->
  <executions>
    <execution>
      <goals>
        <goal>publishVerificationResults</goal>
      </goals>
      <configuration>
        <pactBrokerUrl>https://arpangroup.pactflow.io</pactBrokerUrl>
        <pactBrokerAuthenticationToken>pkqBnpXX3u4o5wErioDeXA</pactBrokerAuthenticationToken>
        <consumerVersion>1.0.0</consumerVersion> <!-- Use the version of your consumer -->
      </configuration>
    </execution>
  </executions>
</plugin>
````