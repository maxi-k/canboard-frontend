(ns canboard-frontend.route
  (:require [secretary.core :as secretary :include-macros true]
            [accountant.core :as accountant]
            [canboard-frontend.data :as data]
            [canboard-frontend.util :as util]))

(defn define-routes
  "Sets the default client side routes for the application"
  [pages]

  (secretary/defroute home-route "/" []
    (data/current-page! (pages "/")))

  (secretary/defroute boards-route "/boards" []
    (data/current-page! (pages "/boards")))

  (secretary/defroute board-route "/boards/:id" {id :id}
    (when (not (nil? (@data/boards (js/parseInt id))))
      (reset! data/current-board-id (js/parseInt id))
      (data/current-page! (pages "/boards/:id"))))

  (secretary/defroute list-route "/boards/:board_id/lists/:id" {board_id :board_id id :id}
    (data/current-page! (pages "/boards/:board_id/lists/:id")))

  (secretary/defroute card-detail-route "/boards/:board_id/lists/:list_id/cards/:card_id" {:keys [board_id list_id card_id]}
    (data/current-page! (pages "/boards/:board_id/lists/:list_id/cards/:card_id")))

  (accountant/configure-navigation!
   {:nav-handler
    (fn [path]
      (secretary/dispatch! path))
    :path-exists?
    (fn [path]
      (secretary/locate-route path))})
  (accountant/dispatch-current!))

(defn goto!
  "Goto the given url. Like clicking on a link"
  [loc]
  (accountant/navigate! loc))
