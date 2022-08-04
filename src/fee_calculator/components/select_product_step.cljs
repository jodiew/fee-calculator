(ns fee-calculator.components.select-product-step
  (:require [re-frame.core :refer [subscribe dispatch]]
            
            [reagent-mui.material.typography :refer [typography]]
            [reagent-mui.material.grid :refer [grid]]
            [reagent-mui.material.button :refer [button]]
            
            [fee-calculator.subs :as subs]
            [fee-calculator.events :as events]))

(def products [{:label "Product A"
                :product :product-a}
               {:label "Product B"
                :product :product-b}
               {:label "Product C"
                :product :product-c}])

(defn select-product-step []
  (let [selected-product @(subscribe [::subs/product])]
    [:<>
     [typography {:variant "h6"
                  :gutter-bottom true}
      "Select your solution"]
     [grid {:container true
            :spacing 3}
      (for [{:keys [label product]} products]
        ^{:key label}
        [grid {:item true
               :xs 12
               :sm 4}
         [button {:variant (if (= selected-product product)
                             "contained"
                             "outlined")
                  :full-width true
                  :on-click #(dispatch [::events/select-product product])}
          label]])]]))