(ns canboard-frontend.prod
  (:require [canboard-frontend.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

;; (core/init!)
