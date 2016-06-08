package com.kong.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

/**
 * 数据去重
 * Created by kong on 2016/4/28.
 */
public class DifferentData {
    public static class DifferentDataMapper extends Mapper<Object, Text, Text, Text> {

        private Text val = new Text();

        @Override
        protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            context.write(value,val);
        }
    }

    public static class DifferentDataReducer extends Reducer<Text, Text, Text, Text> {

        private Text result = new Text();

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("22");
            System.exit(2);
        }

        Job job = Job.getInstance(conf, "DifferentData");
        job.setJarByClass(DifferentData.class);
        job.setMapperClass(DifferentDataMapper.class);
        job.setCombinerClass(DifferentDataReducer.class);
        job.setReducerClass(DifferentDataReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        for (int i = 0; i < otherArgs.length - 1; ++i) {
            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
        }
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[otherArgs.length - 1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
