(ns canboard-frontend.rest
  (:require [ajax.core :refer [GET POST]]))

(def api-base-path (str "/v1/"))

(defn api-call [method path params]

  (method (str api-base-path path) params))

(defn sign-in-user [name passwd callback]
  "Signs in given user with name and password."
  (api-call #'POST "auth/sign_in"
            {:handler callback
             :error-handler callback
             :crossDomain true
             :format :json
             :response-format :json
             :params {:email name
                      :password passwd}}))
