#import "ThreadedDownloader.h"
#import <React/RCTLog.h>

@implementation ThreadedDownloader
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(performThreadedDownload:(NSString *)url
                  timeoutSeconds:(double)timeoutSeconds
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
  RCTLogInfo(@"ThreadedDownloader performThreadedDownload call with %@", url);
  
  dispatch_queue_t queue = dispatch_queue_create("tndm_queue", DISPATCH_QUEUE_CONCURRENT);

  dispatch_async(queue, ^{
    RCTLogInfo(@"ThreadedDownloader performThreadedDownload dispatched in new thread");
    
    NSURL *urlObj = [NSURL URLWithString:url];
    //NSURLSessionConfiguration *sessionConfig = [NSURLSessionConfiguration defaultSessionConfiguration];
    //NSURLSession *session = [NSURLSession sessionWithConfiguration: sessionConfig delegate: self delegateQueue: [NSOperationQueue mainQueue]];
    NSURLSession *session = [NSURLSession sharedSession];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:urlObj cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:timeoutSeconds];
    
    // if ([@"POST" isEqualToString:httpMethod]) {
    //   [request setHTTPMethod:@"POST"];
    // }
    // [request setValue:@"text/plain" forHTTPHeaderField:@"Content-Type"];
    // [requestBody setHTTPBody:[NSData dataWithBytes: [parameters UTF8String]length:strlen([parameters UTF8String])]];
    
    NSURLSessionDataTask *task = [session dataTaskWithRequest:request completionHandler: ^(NSData *data, NSURLResponse *response, NSError *error) {
      if (error == nil) {
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        RCTLogInfo(@"ThreadedDownloader performThreadedDownload received response %ld", (long)httpResponse.statusCode);
        if (httpResponse.statusCode >= 100 && httpResponse.statusCode < 400) {
          NSString *responseString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
          resolve(responseString);
        } else {
          NSString *errorString = [NSString stringWithFormat:@"Error: %ld", (long)httpResponse.statusCode];
          reject(@"error_response", errorString, nil);
        }
      } else {
        RCTLogInfo(@"ThreadedDownloader performThreadedDownload error: %@", error);
        NSString *errorString = [NSString stringWithFormat:@"Error: %@", error];
        reject(@"download_failure", errorString, nil);
      }
    }];
    
    [task resume];
    
  });
}

@end
