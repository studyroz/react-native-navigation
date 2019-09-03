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

+ (NSDictionary *)PresentationStyle { // 如果是iOS13 使用新的Modal样式
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
@end

