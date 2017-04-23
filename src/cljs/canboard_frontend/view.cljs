(ns canboard-frontend.view
  (:require [reagent.core :as r]
            [soda-ash.core :as sa]
            [canboard-frontend.data :as data]
            [canboard-frontend.lang :as lang :refer [translate]]
            [canboard-frontend.api :as api]
            [canboard-frontend.util :as util]
            [canboard-frontend.route :as route]
            [canboard-frontend.view.boards :as boards]
            [canboard-frontend.view.cards :as cards]
            [canboard-frontend.view.parts :as parts :refer [default-template]]))

(defn home-page []
  (default-template
   [:div
    [:h2 (parts/title)]
    [:h2 "Welcome"]
    [:div [:p] [:h4 "User Data"]
     [:p
      (str @data/current-user)]]]))

(defn boards-page [] (default-template
                      boards/overview))
(defn board-page [] (default-template
                     boards/board-page))
(defn card-detail-page []
  (default-template
   boards/board-page
   #(cards/card-detail)))

(defn login-warning
  [& sentences]
  (let [cnt (atom 0)]
    [:div#alert {:class "ui yellow segment"}
     (doall
      (interpose
       [:br {:key ((fn [] (swap! cnt inc) cnt))}]
       (map (fn [s] [:span {:key (str s)}
                    (lang/translate s) "."]) sentences)))]))

(def login-page
  (letfn [(auth []
            (api/authenticate-user! (.-value (util/elem-by-id :login-username))
                                    (.-value (util/elem-by-id :login-password))
                                    (fn []
                                      (when (= "/" (util/current-path))
                                        (route/goto! (route/boards-route))))))
          (key-callback [e] (when (= 13 (.-charCode e)) (auth)))
          (btn-callback [e] (auth))]
    (fn []
      [:div {:class "ui grid centered equal width" :id :login-segment}
       [sa/Segment {:class "twelve"}
        [sa/Image {:size :small
                   :class "ui centered"
                   :src "/img/logo/logo-large.png"}]
        [:div#big-title (parts/title)]
        (if (:wrong-credentials @data/current-user)
          (login-warning :wrong-credentials)
          (when (:unauthorized @data/current-user)
            (login-warning :session-over :login-again)))
        [:div.ui.form
         [:div.field
          [:label (translate :user-name)]
          [:input {:id :login-username :type :text :name :user-name :placeholder "test@example.com"
                   :on-key-press key-callback}]]
         [:div.field
          [:label (translate :password)]
          [:input {:id :login-password :type :password :name :password :placeholder "password"
                   :on-key-press key-callback}]]
         [sa/Button {:on-click btn-callback}
          (translate :do-login)]]]]))
  )
