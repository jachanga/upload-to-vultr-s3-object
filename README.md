# upload-to-vultr-s3-object
Spring Boot S3 upload to vultr S3 Object

Change the endpoint with this code

	EndpointConfiguration endpointConfiguration = new EndpointConfiguration("ewr1.vultrobjects.com", clientRegion);
	s3ClientBuilder.setEndpointConfiguration(endpointConfiguration );
