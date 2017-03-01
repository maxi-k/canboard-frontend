(ns canboard-frontend.rest
  (:require [ajax.core :refer [GET POST]]))

(def server-url "localhost:3000")
(def api-base-path (str server-url "/v1/"))

(defn api-call [method path params]
  (method (str api-base-path method) params))

(defn sign-in-user [name passwd callback]
  "Signs in given user with name and password."
  (api-call #'GET "auth/sign_in"
            {:handler callback
             :error-handler callback
             :params {:email name
                      :password passwd}}))
