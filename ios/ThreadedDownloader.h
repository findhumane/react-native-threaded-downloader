
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNThreadedDownloaderSpec.h"

@interface ThreadedDownloader : NSObject <NativeThreadedDownloaderSpec>
#else
#import <React/RCTBridgeModule.h>

@interface ThreadedDownloader : NSObject <RCTBridgeModule>
#endif

@end
