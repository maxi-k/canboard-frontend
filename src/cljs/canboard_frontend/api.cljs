(ns canboard-frontend.api
  (:require [canboard-frontend.data :as data]
            [canboard-frontend.rest :as rest]
            [canboard-frontend.util :as util]))

(defn update-auth-data!
  [response]
  (swap! data/current-user merge (select-keys :headers util/relevant-auth-headers)))

(defn authenticate-user!
  "Authenticates the given user by the username and password they used to sign in."
  [username passwd]
  (letfn [(convert-response [response]
            (merge (response :headers)
                   {:data (-> response :body :data)}))
          (callback [response]
            (when (== 200 (response :status))
              (reset! data/current-user (convert-response response))))]
    (rest/sign-in-user username passwd callback)))

(defn fetch-boards
  "Fetches the users boards and updates the state.
  Requires authentication."
  []
  (letfn [(callback [response]
            (update-auth-data! response)
            (reset! data/boards (-> response :body :data)))]
    (rest/fetch-boards (data/token-data) callback)))
