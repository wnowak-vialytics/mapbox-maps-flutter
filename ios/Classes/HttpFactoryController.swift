import Foundation
@_spi(Experimental) import MapboxMaps
import UIKit
class HttpFactoryController: NSObject, HttpFactorySettingsInterface {
    
    func setInterceptor(options: [HttpInterceptorOptions?]) throws {
        HttpServiceFactory.getInstance().setInterceptorForInterceptor(
            HttpFactoryInterceptor(
                pluginVersion: pluginVersion,
                options: options
            )
        )
    }
    
    func handleSetInterceptor(methodCall: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let arguments = methodCall.arguments as? [String: Any] else { return }
        guard let options = arguments["options"] as? [HttpInterceptorOptions?] else { return }
        do {
            try setInterceptor(options: options)
        } catch {
            result(FlutterError(code: "HttpFactoryController", message: "Failed to set interceptor", details: nil))
        }
        result(nil)
    }
    
    final class HttpFactoryInterceptor: HttpServiceInterceptorInterface {
        private var pluginVersion: String
        private var options: [HttpInterceptorOptions?]
        
        init(pluginVersion: String, options: [HttpInterceptorOptions?]) {
            self.pluginVersion = pluginVersion
            self.options = options
        }
        
        func onRequest(for request: HttpRequest) -> HttpRequest {
            if let oldUserAgent = request.headers[HttpHeaders.userAgent] {
                request.headers[HttpHeaders.userAgent] = "\(oldUserAgent) FlutterPlugin/\(self.pluginVersion)"
            }
            
            for option in options {
                if option == nil {
                    continue
                }
                let urlRegex = try! NSRegularExpression(pattern: option!.urlRegex)
                let range = NSRange(location: 0, length: request.url.utf16.count)
                if urlRegex.firstMatch(in: request.url, options: [], range: range) != nil {
                    let headers = option?.headers ?? []
                    for header in headers {
                        request.headers[header!.key] = header!.value
                    }
                }
            }
            
            return request
        }
        
        func onDownload(forDownload download: DownloadOptions) -> DownloadOptions {
            return download
        }
        
        func onResponse(for response: HttpResponse) -> HttpResponse {
            return response
        }
    }
    
    private var pluginVersion: String
    
    init(withPluginVersion pluginVersion: String) {
        self.pluginVersion = pluginVersion
    }
}
