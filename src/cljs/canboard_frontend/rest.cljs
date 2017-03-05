(ns canboard-frontend.rest
  (:require [ajax.core :as ajax :refer [GET POST]]
            [ajax.protocols :as ajaxp]
            [goog.json :as json]))

(def api-base-path (str "/v1/"))

(defn api-call [method path params]
  (method (str api-base-path path) params))

(defn parse-response-json-with-header
  "Parses the given response Xhrio Object to JSON.
  Unline the cljs-ajax default, also parses the header:
  {:headers {..}, :body {...}, :status}"
  [header-keys xhrio]
  {:body (-> xhrio ajaxp/-body json/parse (js->clj :keywordize-keys true))
   :status (ajaxp/-status xhrio)
   :headers (reduce (fn [c key] (assoc c (keyword key)
                                      (ajaxp/-get-response-header xhrio key)))
                    {} header-keys)})

(defn json-header-response-format [header-keys]
  "The response format used to read responses with headers."
  {:read (partial parse-response-json-with-header header-keys)
   :description "JSON headers"
   :content-type ["application/json"]})

(defn sign-in-user [name passwd callback]
  "Signs in given user with name and password."
  (api-call #'POST "auth/sign_in"
            {:handler callback
             :error-handler callback
             :format :json
             :response-format (json-header-response-format ["access-token" "token-type" "client"])
             :params {:email name
                      :password passwd}}))
