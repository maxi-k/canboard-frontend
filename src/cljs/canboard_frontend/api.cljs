(ns canboard-frontend.api
  (:require [canboard-frontend.data :as data]
            [canboard-frontend.rest :as rest]
            [canboard-frontend.util :as util]))

(defn update-auth-data!
  [response]
  (if (== (response :status) 401)
    (swap! data/current-user assoc :unauthorized true)
    (swap! data/current-user merge (select-keys :headers util/relevant-auth-headers))))

(defn authenticate-user!
  "Authenticates the given user by the username and password they used to sign in."
  [username passwd after]
  (letfn [(convert-response [response]
            (merge (response :headers)
                   {:data (-> response :body :data)}))
          (callback [response]
            (when (== 200 (response :status))
              (reset! data/current-user (convert-response response))
              (after)))]
    (rest/sign-in-user username passwd callback)))

(defn fetch-boards!
  "Fetches the users boards and updates the state.
  Requires authentication."
  [after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (when-let [boards (get-in response [:body :data])]
              (reset! data/boards boards)
              (after)))]
    (rest/fetch-boards (data/token-data) callback)))

(defn create-board!
  "Creates a new board belonging to the current user.
  Requires authentication."
  [board-data after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (util/log response)
            (swap! data/boards conj (-> response :body :data))
            (after))]
    (rest/create-board! board-data (data/token-data) callback)))
