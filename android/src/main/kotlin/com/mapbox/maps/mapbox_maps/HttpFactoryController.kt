package com.mapbox.maps.mapbox_maps

import com.mapbox.common.DownloadOptions
import com.mapbox.common.HttpHeaders
import com.mapbox.common.HttpRequest
import com.mapbox.common.HttpResponse
import com.mapbox.common.HttpServiceFactory
import com.mapbox.common.HttpServiceInterceptorInterface
import com.mapbox.maps.pigeons.FLTHttpFactorySettings
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class HttpFactoryController(private val pluginVersion: String) :
  FLTHttpFactorySettings.HttpFactorySettingsInterface {
  override fun setInterceptor(options: MutableList<FLTHttpFactorySettings.HttpInterceptorOptions>) {
    HttpServiceFactory.getInstance().setInterceptor(
      object : HttpServiceInterceptorInterface {
        override fun onRequest(request: HttpRequest): HttpRequest {
          request.headers[HttpHeaders.USER_AGENT] =
            "${request.headers[HttpHeaders.USER_AGENT]} Flutter Plugin/$pluginVersion"
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
          return request
        }

        override fun onDownload(download: DownloadOptions): DownloadOptions {
          return download
        }

        override fun onResponse(response: HttpResponse): HttpResponse {
          return response
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
