


import 'package:nexring_flutter_platform/android_implementation/nex_ring_android_platform_interface.dart';

class NexRingFlutterPlatform {
  Future<bool> isBleSupported() async {
    return await NexRingAndroidPlatform.instance.isBleSupported();
  }
}
