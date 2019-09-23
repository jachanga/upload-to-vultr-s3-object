package com.mytest.app.vultr.upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.stockcrowd.utils.services.exceptions.InvalidFileFormatException;
import com.stockcrowd.utils.services.exceptions.InvalidFileSizeException;

@Service
public class UploadServiceToVultr {

	@Value("${app.aws.iam.accesskey}")
	private String accessKey;

	@Value("${app.aws.iam.secretkey}")
	private String secretKey;

	@Value("${app.aws.s3.clientregion}")
	private String clientRegion;

	@Value("${app.aws.s3.bucketname}")
	private String bucketName;

	@Value("${spring.servlet.multipart.max-file-size}")
	private Integer maxFileSize;

	public String uploadImage(MultipartFile file) throws IOException, InvalidFileFormatException,
			InvalidFileSizeException, AmazonServiceException, SdkClientException {
		byte[] bytes = file.getBytes();
		String fileObjKeyName = file.getOriginalFilename();

		if (!file.getContentType().contains("image/")) {
			throw new InvalidFileFormatException();
		} else if (file.getSize() > maxFileSize) {
			throw new InvalidFileSizeException();
		} else {

			BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
			@SuppressWarnings("rawtypes")
			AmazonS3ClientBuilder s3ClientBuilder = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(awsCreds));
			

			EndpointConfiguration endpointConfiguration = new EndpointConfiguration("ewr1.vultrobjects.com", clientRegion);
			s3ClientBuilder.setEndpointConfiguration(endpointConfiguration );
			AmazonS3 s3Client = s3ClientBuilder.build();

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

			return "https://" + clientRegion + ".vultrobjects.com/" + bucketName + "/" + fileObjKeyName;
		}
	}
	
	
	
}
