(ns canboard-frontend.api.rest-util
  (:require [ajax.protocols :as ajaxp]
            [ajax.core :as ajax :refer [GET POST PUT DELETE PATCH]]
            [goog.json :as json]
            [canboard-frontend.util :as util]
            [canboard-frontend.config :as config]))

(defn api-call
  ([method path params]
   (api-call method path params true))
  ([method path with-basepath params]
   (if with-basepath
     (method (apply util/build-path (config/get-config :api-base-path) (util/wrap path)) params)
     (method (apply util/build-path (util/wrap path)) params))))

(defn parse-response-json-with-header
  "Parses the given response Xhrio Object to JSON.
  Unline the cljs-ajax default, also parses the header:
  {:headers {..}, :body {...}, :status}"
  [header-keys xhrio]
  {:body (-> xhrio ajaxp/-body json/parse (js->clj :keywordize-keys true))
   :status (ajaxp/-status xhrio)
   :headers (reduce (fn [c key] (assoc c (keyword key)
                                      (ajaxp/-get-response-header xhrio (name key))))
                    {} header-keys)})

(defn json-header-response-format
  "The response format used to read responses with headers."
  [header-keys]
  {:read (partial parse-response-json-with-header header-keys)
   :description "JSON headers"
   :content-type ["application/json"]})
