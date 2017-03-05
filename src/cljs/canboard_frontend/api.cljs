(ns canboard-frontend.api
  (:require [canboard-frontend.data :as data]
            [canboard-frontend.rest :as rest]
            [canboard-frontend.util :as util]))

(defn authenticate-user!
  "Authenticates the given user by the username and password they used to sign in."
  [username passwd]
  (letfn [(convert-response [response]
            (merge (response :headers)
                   {:data (-> response :body :data)}))
          (callback [response]
            (when (== 200 (response :status))
              (data/data! [:current-user] (convert-response response))))]
    (rest/sign-in-user username passwd callback)))
