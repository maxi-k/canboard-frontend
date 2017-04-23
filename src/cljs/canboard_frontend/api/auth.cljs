(ns canboard-frontend.api.auth
  (:require [canboard-frontend.config :as config]
            [canboard-frontend.api.rest-util :as rutil]
            [canboard-frontend.util :as util]
            [ajax.core :as ajax :refer [GET POST PUT DELETE PATCH]]
            ))

(defn auth-strategy
  "Returns the configured auth strategy"
  []
  (config/get-config :auth :implementation))

(defn request-location
  [location]
  (condp = (keyword location)
    :body :params
    :headers :headers))

(defn standard-authenticate
  [endpoint json-root params-content header-params callback]
  (let [params (if (or (empty? json-root) (= "/" json-root))
                 params-content
                 {(keyword json-root) params-content})]
    (rutil/api-call #'POST [endpoint] false
                    {:handler callback
                     :error-handler callback
                     :format :json
                     :response-format (rutil/json-header-response-format header-params)
                     :params params})))

(defprotocol AuthStrategy
  (auth-authenticate [self username secret callback]
    "Authenticates the user using the given username and secret by
returning an auth-data map that is passed to the callback given.")
  (auth-convert-response
    [self response]
    [self data response]
    "Takes (the old auth data and) the response returned by an request and produces some new auth-data")
  (auth-api-call
    [self method path auth-data params]
    [self method path auth-data with-basepath params]
    "Performs an authenticated call to the api with given method, to given path.")
  (auth-logout [self auth-data callback] "Logs out the current user."))

(defrecord SessionAuth [endpoint json-root]
  AuthStrategy
  (auth-authenticate
    [self username secret callback]
    (standard-authenticate
     (:endpoint self)
     (:json-root self)
     {:email username :password secret}
     []
     callback))
  (auth-convert-response
    [self response]
    (auth-convert-response self (response :body) response))
  (auth-convert-response
    [self data response]
    (util/log (str response))
    (merge data {:unauthorized (>= (:status response) 400)
                 :wrong-credentials (and (>= (:status response) 400)
                                         (:failure response))}))
  (auth-api-call
    [self method path auth-data params]
    (auth-api-call self method path auth-data true params))
  (auth-api-call
    [self method path auth-data with-basepath params]
    ;; Cookie should be set automatically by the
    (rutil/api-call
     method path with-basepath
     (merge
      params
      {:response-format (rutil/json-header-response-format [])
       :with-credentials true})))
  (auth-logout [self auth-data callback]
    (auth-api-call self #'DELETE (:endpoint self) auth-data false
                   {:handler callback
                    :error-handler callback})))

(defrecord TokenAuth [endpoint location json-root relevant-params]
  AuthStrategy
  (auth-authenticate [self username secret callback]
    (standard-authenticate
     (:endpoint self)
     (:json-root self)
     {:email username :password secret}
     (:relevant-params self)
     callback))
  (auth-convert-response [self response]
    ;; If there is no old data, merge with the response body which contains user information
    (auth-convert-response self (response :body) response))
  (auth-convert-response
    [self data response]
    ;; If the user was previously unauthorized, dissoc the identifier
    ;; If the response returned 401 unauthorized, assoc the identifier
    (let [relevant (select-keys (response (:location self))
                                (:relevant-params self) )]
      (merge data
             ;; Put the relevant parameters into the data map
             relevant
             ;; Whether the user is authorized and whether it is
             ;; because the token has expired or they supplied wrong credentials
             {:unauthorized (and (>= (:status response) 400)
                                 (every? some? relevant))
              :wrong-credentials (and (>= (:status response 400))
                                      (every? nil? relevant))})))
  (auth-api-call
    [self method path auth-data params]
    (auth-api-call self method path auth-data true params))

  (auth-api-call [self method path auth-data with-basepath params]
    (let [token-data (update
                      (reduce (fn [h key] (assoc h (name key) (auth-data key)))
                              {} (:relevant-params self))
                      "expiry"
                      js/parseInt)]
      (rutil/api-call
       method path with-basepath
       (assoc
        (update params (request-location (:location self)) #(merge token-data %))
        :response-format (rutil/json-header-response-format (:relevant-params self))))))
  (auth-logout [self auth-data callback] (callback)))

(defn authenticate
  [username secret callback]
  (auth-authenticate (auth-strategy) username secret callback))

(defn convert-response
  ([request]
   (auth-convert-response (auth-strategy) request))
  ([data request]
   (auth-convert-response (auth-strategy) data request)))

(defn api-call
  [method path auth-data params]
  (auth-api-call (auth-strategy) method path auth-data params))

(defn logout
  [auth-data callback]
  (auth-logout (auth-strategy) auth-data callback))

(defmulti make-auth-strategy
  "Initializes the authentication strategy according to the config received."
  (fn [auth-config] (keyword (-> auth-config :strategy :type))))

(defmethod make-auth-strategy :token
  [{:keys [endpoint json-root]
    {:keys [parameter-location auth-parameters]} :strategy}]
  (TokenAuth. endpoint
              parameter-location
              json-root
              (map keyword auth-parameters)))


(defmethod make-auth-strategy :session
  [{:keys [endpoint json-root]}]
  (SessionAuth. endpoint json-root))
