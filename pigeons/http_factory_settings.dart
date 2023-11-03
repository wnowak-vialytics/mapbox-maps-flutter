import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(
  PigeonOptions(
    dartOut: 'lib/src/pigeons/http_factory_settings.dart',
    javaOut:
        'android/src/main/java/com/mapbox/maps/pigeons/FLTHttpFactorySettings.java',
    javaOptions: JavaOptions(
      package: 'com.mapbox.maps.pigeons',
    ),
    swiftOut: 'ios/Classes/HttpFactorySettings.swift',
  ),
)
class HttpInterceptorOptions {
  /// Options for configuring custom HTTP headers. Given set of [headers] will
  /// be applied for any request which URL matches a regular expression provided
  /// in the [urlRegex] field. By default, the [urlRegex] field is set to match
  /// any URL.
  HttpInterceptorOptions({
    this.urlRegex = '.*',
    this.headers,
  });

  /// A pattern to match URLs against. Defaults to `.*` which matches any URL.
  String urlRegex;

  /// A list of headers to apply to any request which URL matches the [urlRegex]
  /// pattern.
  List<HttpHeader?>? headers;
}

class HttpHeader {
  /// A pair of HTTP header name and value.
  HttpHeader({
    required this.key,
    required this.value,
  });

  /// HTTP header name.
  String key;

  /// HTTP header value.
  String value;
}

@HostApi()
abstract class HttpFactorySettingsInterface {
  /// Sets a list of [HttpInterceptorOptions] to be used by the HTTP client.
  void setInterceptor(List<HttpInterceptorOptions?> options);
}
