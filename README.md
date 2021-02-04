# upload-to-vultr-s3-object
Spring Boot S3 upload to vultr S3 Object

Change the endpoint with this code

	EndpointConfiguration endpointConfiguration = new EndpointConfiguration("ewr1.vultrobjects.com", clientRegion);
	s3ClientBuilder.setEndpointConfiguration(endpointConfiguration );


# Simple spring describe upload to vultr s3 bucket

	public String uploadFile(MultipartFile file) throws IOException, InvalidFileFormatException,
				InvalidFileSizeException, AmazonServiceException, SdkClientException {

		byte[] bytes = file.getBytes();
		String fileObjKeyName = file.getOriginalFilename();
		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(new EndpointConfiguration("ewr1.vultrobjects.com", null))
				.withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(file.getContentType());
		// metadata.addUserMetadata("x-amz-meta-title", "someTitle");
		metadata.setContentLength(file.getSize());

		InputStream inputStream = new ByteArrayInputStream(bytes);
		PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, inputStream, metadata);

		AccessControlList accessControlList = new AccessControlList();
		accessControlList.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		request.setAccessControlList(accessControlList);

		s3Client.putObject(request);

		return "https://ewr1.vultrobjects.com/" + bucketName + "/" + fileObjKeyName;

	}


# Imports

	<dependency>
	    <groupId>com.amazonaws</groupId>
	    <artifactId>aws-java-sdk-s3</artifactId>
	    <version>1.11.343</version>
	</dependency>
