import logging
import boto3
from botocore.exceptions import ClientError
import os
import json 
import csv
    
#cloud_function(platforms=[Platform.AWS], memory=512, config=config)
def yourFunction(request, context):
	import time

	if ('bucketName' in request and 'keyName' in request):
		s3 = boto3.client('s3')
		bucketname = request['bucketName']
		filename = request['keyName']
		response = s3.get_object(Bucket=bucketname, Key=filename)
		csvdata = response['Body'].read().decode('utf-8')
		modifyCSV(csvdata, filename)
		upload_file("/tmp/mod " + filename, bucketname);
	else:
		return{"Error": "No valid data"}
	return {"Success":"/tmp/mod " + filename}

def modifyCSV(csvData, filename):
	csvRows = csvData.split("\r\n")
	rows = []
	for row in csvRows:
		writerow = ""
		rowSplit = row.split(",")
		for col in rowSplit:
			append = col
			if col == "C":
				append = "Critical"
			if col == "L":
				append = "Low"
			if col == "M":
				append = "Medium"
			if col == "H":
				append = "High"
			writerow += append + ","
		rows.append(writerow[:-1] +"\n")
	write = open("/tmp/mod "+filename, 'w')
	for line in rows:
		write.write(line)
	write.close()
	


def upload_file(file_name, bucket, object_name=None):
    """Upload a file to an S3 bucket

    :param file_name: File to upload
    :param bucket: Bucket to upload to
    :param object_name: S3 object name. If not specified then file_name is used
    :return: True if file was uploaded, else False
    """

    # If S3 object_name was not specified, use file_name
    if object_name is None:
        object_name = os.path.basename(file_name)

    # Upload the file
    s3_client = boto3.client('s3')
    try:
        response = s3_client.upload_file(file_name, bucket, object_name)
    except ClientError as e:
        logging.error(e)
        return False
    return True

