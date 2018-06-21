#import "Pcm16khzAudioRecorderPlugin.h"
#import <pcm16khz_audio_recorder/pcm16khz_audio_recorder-Swift.h>

@implementation Pcm16khzAudioRecorderPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPcm16khzAudioRecorderPlugin registerWithRegistrar:registrar];
}
@end
