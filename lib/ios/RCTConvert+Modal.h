#import <React/RCTConvert.h>

@interface RCTConvert (Modal)

@end

@implementation RCTConvert (Modal)

RCT_ENUM_CONVERTER(UIModalTransitionStyle,
				   (@{@"coverVertical": @(UIModalTransitionStyleCoverVertical),
					  @"flipHorizontal": @(UIModalTransitionStyleFlipHorizontal),
					  @"crossDissolve": @(UIModalTransitionStyleCrossDissolve),
					  @"partialCurl": @(UIModalTransitionStylePartialCurl)
					  }), UIModalTransitionStyleCoverVertical, integerValue)

RCT_ENUM_CONVERTER(UIModalPresentationStyle,
				   ([self PresentationStyle]), UIModalPresentationFullScreen, integerValue)


#if __IPHONE_OS_VERSION_MAX_ALLOWED >=  130000 // iOS 13以上的系统版本 增加一种modal Automatic样式
+ (NSDictionary *)PresentationStyle {
    if (@available(iOS 13.0, *)) {
        return @{
                @"fullScreen": @(UIModalPresentationFullScreen),
                @"pageSheet": @(UIModalPresentationPageSheet),
                @"formSheet": @(UIModalPresentationFormSheet),
                @"currentContext": @(UIModalPresentationCurrentContext),
                @"custom": @(UIModalPresentationCustom),
                @"overFullScreen": @(UIModalPresentationOverFullScreen),
                @"overCurrentContext": @(UIModalPresentationOverCurrentContext),
                @"popover": @(UIModalPresentationPopover),
                @"none": @(UIModalPresentationNone),
                @"automatic":@(UIModalPresentationAutomatic),
                };
    } else {
        return @{
                @"fullScreen": @(UIModalPresentationFullScreen),
                @"pageSheet": @(UIModalPresentationPageSheet),
                @"formSheet": @(UIModalPresentationFormSheet),
                @"currentContext": @(UIModalPresentationCurrentContext),
                @"custom": @(UIModalPresentationCustom),
                @"overFullScreen": @(UIModalPresentationOverFullScreen),
                @"overCurrentContext": @(UIModalPresentationOverCurrentContext),
                @"popover": @(UIModalPresentationPopover),
                @"none": @(UIModalPresentationNone),
                @"automatic":@(UIModalPresentationFullScreen),
                };
    }
}
#else
+ (NSDictionary *)PresentationStyle {
        return @{
                @"fullScreen": @(UIModalPresentationFullScreen),
                @"pageSheet": @(UIModalPresentationPageSheet),
                @"formSheet": @(UIModalPresentationFormSheet),
                @"currentContext": @(UIModalPresentationCurrentContext),
                @"custom": @(UIModalPresentationCustom),
                @"overFullScreen": @(UIModalPresentationOverFullScreen),
                @"overCurrentContext": @(UIModalPresentationOverCurrentContext),
                @"popover": @(UIModalPresentationPopover),
                @"none": @(UIModalPresentationNone),
                @"automatic":@(UIModalPresentationFullScreen),
                };
}
#endif

@end

