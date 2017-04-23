(ns canboard-frontend.api
  (:require [canboard-frontend.data :as data]
            [canboard-frontend.util :as util]
            [canboard-frontend.config :as config]
            [canboard-frontend.api.rest :as rest]
            [canboard-frontend.api.conversion :as conv]
            [canboard-frontend.api.auth :as auth]))

(defn update-auth-data!
  [response]
  (swap! data/current-user auth/convert-response response))

(defn authenticate-user!
  "Authenticates the given user by the username and password they used to sign in."
  [username passwd after]
  (letfn [(callback [response]
            (reset! data/current-user (auth/convert-response response))
            (after))]
    (auth/authenticate username passwd callback)))

(defn logout!
  "Logs out the current user."
  []
  (auth/logout @data/current-user #(reset! data/current-user nil)))

(defn fetch-boards!
  "Fetches the users boards and updates the state.
  Requires authentication."
  [after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (when-let [boards (get-in response [:body :boards])]
              (reset! data/boards (conv/conv-boards boards))
              (after)))]
    (rest/fetch-boards (data/token-data) callback)))

(defn create-board!
  "Creates a new board belonging to the current user.
  Requires authentication."
  [board-data after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (when-let [board (get-in response [:body :board])]
              (swap! data/boards assoc (:id board) board)
              (after)))]
    (rest/create-board! board-data (data/token-data) callback)))

(defn delete-board!
  "Deletes the board given by its id."
  [id after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (fetch-boards! after))]
    (rest/delete-board! id (data/token-data) callback)))

(defn fetch-board-lists!
  "Fetches the lists for the board with given id."
  [id after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (when-let [data (get-in response [:body :lists])]
              (reset! data/lists (conv/conv-lists data))
              (after)))]
    (rest/fetch-lists id (data/token-data) callback)))

(defn create-list!
  "Creates a list with given data in the given board."
  [list-data id after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (when-let [list (get-in response [:body :list])]
              (swap! data/lists assoc (:id list) list)
              (after)))]
    (rest/create-list! list-data id (data/token-data) callback)))

(defn delete-list!
  "Deletes the list given by its id."
  [board-id list-id after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (fetch-board-lists! board-id after))]
    (rest/delete-list! board-id list-id (data/token-data) callback)))

(defn fetch-list-cards!
  "Fetches the cards of a single list."
  [board-id list-id after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (when-let [data (get-in response [:body :cards])]
              (swap! data/lists assoc-in [list-id :cards] (conv/conv-cards data))
              (after)))]
    (rest/fetch-cards board-id list-id (data/token-data) callback)))

(defn create-card!
  "Creates a card with given data in the given list (by board-id list-id)."
  [card-data board-id list-id after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (when-let [card (get-in response [:body :card])]
              (data/data! [:boards board-id
                           :lists list-id
                           :cards (:id card)]
                          (conv/conv-card card))
              (after)))]
    (rest/create-card! card-data board-id list-id (data/token-data) callback)))

(defn delete-card!
  "Deletes the card given by its id."
  [board-id list-id card-id after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (fetch-list-cards! board-id list-id after))]
    (rest/delete-card! board-id list-id card-id (data/token-data) callback)))

(defn update-card!
  "Updates the card given by board-, list- and card-id.
  Only updates the attributes given by attrs."
  [board-id list-id card-id keys after]
  (letfn [(callback [response]
            (update-auth-data! response)
            (when-let [card (get-in response [:body :card])]
              (data/data! [:boards board-id
                           :list list-id
                           :cards (:id card)]
                          (conv/conv-card card)))
            (after))]
    (-> @data/app-state
        (get-in [:boards board-id :lists list-id :cards card-id])
        (select-keys keys)
        (#(rest/update-card! board-id list-id card-id % (data/token-data) callback)))))
