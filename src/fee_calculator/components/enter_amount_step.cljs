(ns fee-calculator.components.enter-amount-step
  (:require [reagent-mui.material.typography :refer [typography]]
            [reagent-mui.material.grid :refer [grid]]

            [fee-calculator.subs :as subs]
            [fee-calculator.events :as events]
            [fee-calculator.components.custom-text-fields :refer [currency-text-field]]
            [fee-calculator.components.fee-slider :refer [fee-slider]]))

(defn enter-amount-step []
  [:<>
   [typography {:variant "h6"
                :gutter-bottom true}
    "Enter your investment amount"]
   [grid {:container true
          :spacing 3}
    [grid {:item true
           :xs 12
           :sm 4}
     [currency-text-field {:sub-path [::subs/investment-amount]
                           :disp [::events/investment-amount]}]]]
   [typography {:variant "h6"
                :gutter-bottom true
                :sx {:mt 3}}
    "Your administration fee range"]
   [fee-slider]])
