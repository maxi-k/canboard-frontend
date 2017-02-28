(ns canboard-frontend.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [canboard-frontend.core-test]))

(doo-tests 'canboard-frontend.core-test)
