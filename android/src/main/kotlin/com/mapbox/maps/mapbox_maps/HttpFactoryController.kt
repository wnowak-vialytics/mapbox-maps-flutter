package com.mapbox.maps.mapbox_maps

import com.mapbox.common.DownloadOptions
import com.mapbox.common.HttpHeaders
import com.mapbox.common.HttpRequest
import com.mapbox.common.HttpRequestOrResponse
import com.mapbox.common.HttpResponse
import com.mapbox.common.HttpServiceFactory
import com.mapbox.common.HttpServiceInterceptorInterface
import com.mapbox.common.HttpServiceInterceptorRequestContinuation
import com.mapbox.common.HttpServiceInterceptorResponseContinuation
import com.mapbox.maps.pigeons.FLTHttpFactorySettings
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class HttpFactoryController(private val pluginVersion: String) :
  FLTHttpFactorySettings.HttpFactorySettingsInterface {
  override fun setInterceptor(options: MutableList<FLTHttpFactorySettings.HttpInterceptorOptions>) {
    HttpServiceFactory.getInstance().setInterceptor(
      object : HttpServiceInterceptorInterface {
        override fun onRequest(
          request: HttpRequest,
          continuation: HttpServiceInterceptorRequestContinuation
        ) {
          request.headers[HttpHeaders.USER_AGENT] =
            "${request.headers["user-agent"]} Flutter Plugin/$pluginVersion"
          for (option: FLTHttpFactorySettings.HttpInterceptorOptions? in options) {
            if (option == null) {
              continue
            }
            if (request.url.matches(
                Regex(
                  option.urlRegex,
                  RegexOption.IGNORE_CASE
                )
              )
            ) {
              val headers: List<FLTHttpFactorySettings.HttpHeader>? =
                option.headers
              if (headers != null) {
                for (header: FLTHttpFactorySettings.HttpHeader in headers) {
                  request.headers[header.key] = header.value
                }
              }
            }
          }
          continuation.run(HttpRequestOrResponse(request))
        }

        override fun onResponse(
          response: HttpResponse,
          continuation: HttpServiceInterceptorResponseContinuation
        ) {
          continuation.run(response)
        }
      }
    )
  }

  fun handleSetInterceptor(call: MethodCall, result: MethodChannel.Result) {
    val options =
      call.argument<List<FLTHttpFactorySettings.HttpInterceptorOptions>>("options")
    if (options != null) {
      setInterceptor(options.toMutableList())
    }
    result.success(null)
  }
}
