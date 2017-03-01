(ns canboard-frontend.api
  (:require [canboard-frontend.data :as data]
            [canboard-frontend.rest :as rest]
            [canboard-frontend.util :as util]))

(defn authenticate-user!
  "Authenticates the given user by the username and password they used to sign in."
  [username passwd]
  (letfn [(callback [response]
            (util/log response))]
    (rest/sign-in-user username passwd callback)))
