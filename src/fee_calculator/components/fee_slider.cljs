(ns fee-calculator.components.fee-slider
  (:require [re-frame.core :refer [subscribe]]
            [reagent.core :refer [as-element]]

            [reagent-mui.material.typography :refer [typography]]
            [reagent-mui.material.box :refer [box]]
            [reagent-mui.material.stack :refer [stack]]
            [reagent-mui.material.slider :refer [slider]]
            
            [fee-calculator.subs :as subs]))

(defn fee-slider []
  (let [min-fee-amount @(subscribe [::subs/min-fee-amount])
        min-fee-percent @(subscribe [::subs/min-fee-percent])
        max-fee-amount @(subscribe [::subs/max-fee-amount])
        max-fee-percent @(subscribe [::subs/max-fee-percent])]
    [box {:sx {:mx 10
               :my 5}}
     [slider {:value @(subscribe [::subs/administration-fee-amount])
              :value-label-display "on"
              :value-label-format #(str "$" %)
              :min min-fee-amount
              :max max-fee-amount
              :marks [{:value min-fee-amount
                       :label (as-element [stack {:align-items "center"}
                                           [typography "Minimum Fee"]
                                           [typography (str "$" (.toFixed min-fee-amount 0) " p.a."
                                                            " or " min-fee-percent "% p.a.")]])}
                      {:value max-fee-amount
                       :label (as-element [stack {:align-items "center"}
                                           [typography "Maximum Fee"]
                                           [typography (str "$" (.toFixed max-fee-amount 0) " p.a."
                                                            " or " max-fee-percent "% p.a.")]])}]}]]))
