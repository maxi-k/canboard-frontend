(ns canboard-frontend.rest
  (:require [canboard-frontend.util :as util]
            [ajax.core :as ajax :refer [GET POST PUT DELETE]]
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

(defn json-header-response-format
  "The response format used to read responses with headers."
  [header-keys]
  {:read (partial parse-response-json-with-header header-keys)
   :description "JSON headers"
   :content-type ["application/json"]})

(def json-auth-headers-response-format
  (json-header-response-format util/relevant-auth-headers-str))

(defn format-auth-header
  "Formats the given header with the auth-token data."
  [header auth-data]
  (merge auth-data header))

(defn auth-api-call
  [method path auth-data params]
  (method (str api-base-path path)
          (assoc
           (update params :headers format-auth-header auth-data)
           :response-format json-auth-headers-response-format)))

(defn sign-in-user
  "Signs in given user with name and password."
  [name passwd callback]
  (api-call #'POST "auth/sign_in"
            {:handler callback
             :error-handler callback
             :format :json
             :response-format json-auth-headers-response-format
             :params {:email name
                      :password passwd}}))

(defn fetch-boards
  "Fetches the current users boards"
  [auth-data callback]
  (auth-api-call #'GET "boards" auth-data
                 {:handler callback
                  :error-handler callback
                  :format :json
                  :response-format json-auth-headers-response-format}))

(defn create-board!
  "Creates a new board with the given data."
  [data auth-data callback]
  (auth-api-call #'POST "boards" auth-data
                 {:handler callback
                  :error-handler callback
                  :format :json
                  :response-format json-auth-headers-response-format
                  :params data}))

(defn delete-board!
  "Deletes the board with given id"
  [id auth-data callback]
  (auth-api-call #'DELETE (str "boards/" id) auth-data
                 {:handler callback
                  :error-handler callback
                  :response-format json-auth-headers-response-format}))

(defn fetch-board
  "Fetches the data for the board with the given id."
  [id auth-data callback]
  (auth-api-call #'GET (str "boards/" id) auth-data
                 {:handler callback
                  :error-handler callback
                  :response-format json-auth-headers-response-format}))
