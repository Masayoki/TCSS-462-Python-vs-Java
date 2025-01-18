# TCSS-462-Python-vs-Java

In the ever-evolving landscape of cloud computing, choosing the right programming language for serverless computing tasks can significantly impact performance. Java and Python are the two most commonly used programming languages in Amazon Web Services (AWS) [2]. This research delves into the performance evaluation of Java and Python in an AWS Lambda environment, focusing on data processing tasks involving a CSV file containing 5000 rows. The key components employed in this study include AWS Lambda for serverless execution, Amazon RDS for database management, and Amazon S3 for data storage.Embarking on an exploration of our project, our team immerses in the dynamic intersection of programming languages and diverse architectures, notably the contrasting realms of ARMS and x86. In this project, we focus on the comparison of Java and Python - within the
frameworks of ARM and x86 architectures. With the goal of comparing the two languages with the different architectures, we
plan to find the differences of different runtimes, how much each cost, the performance, and so on.

RUNNING THE CODE:

ALL database RDS information is hard-coded into the projects. You will have to edit the source code files and change the variables where the database information is stored.

For the Java projects, you will have to install the mysql connector dependency. Use netbeans to build a project jar that way and upload.

For the Transform and Load steps in JAVA, you need to specify the 'bucketname' and the 'filename' in the json input, while for PYTHON, it's 'bucketName' and 'keyName'.

The Transform steps create the newly modified CSV file in the given bucket as "mod {previousFileName}".
If you gave the program "5000 Sales Records.csv", it would create "mod 5000 Sales Records.csv" in your bucket.

For the Query step in Python, you can Specify Aggregates using Sum, Avg, Min, Max, or Count fields in the json input, as well as setting filters using Region, ItemType, SalesChannel, or OrderPriority fields.

For the Query step in Java, you have to specify Aggregates or filters in the respective field as a comma-separated list of sql syntax. For example, for aggregates, you would specify it as "aggregates":"max(ItemsSold),min(ItemsSold)" and for filters, "filters":"ItemType=\"Snacks\",OrderPriority=\"Critical\""

