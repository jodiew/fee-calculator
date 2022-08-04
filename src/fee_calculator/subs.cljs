(ns fee-calculator.subs
  (:require
   [re-frame.core :refer [reg-sub]]
   [clojure.string :refer [includes?]]
   
   [fee-calculator.helpers :refer [number->currency
                                   add-string-numbers]]
   [fee-calculator.calculator :refer [investment-percent]]
  ;;  [fee-calculator.services.a-fees-calculator :refer [super-fees-calculator]]
  ;;  [fee-calculator.services.idps-fees-calculator :refer [idps-fees-calculator]]
   [fee-calculator.services.a-fees-calculator :refer [a-fees-calculator]]
   [fee-calculator.services.b-fees-calculator :refer [b-fees-calculator]]))

;; -- Extractors --------------------------------------------------

(reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(reg-sub
 ::product
 (fn [db]
   (:product db)))

(reg-sub
 ::investment-amount
 (fn [db]
   (:investment-amount db)))

(reg-sub
 ::allocations
 (fn [db]
   (:allocations db)))

(reg-sub
 ::active-step
 (fn [db]
   (:active-step db)))

(reg-sub
 ::securites
 (fn [db]
   (:securities db)))

(reg-sub
 ::search-string
 (fn [db]
   (:search-string db)))

(reg-sub
 ::admin-fee-option
 (fn [db]
   (:admin-fee-option db)))

(reg-sub
 ::asset-class-option
 (fn [db]
   (:asset-class-option db)))

(reg-sub
 ::open-company-funds
 (fn [db]
   (:open-company-funds db)))

(reg-sub
 ::open-managed-funds
 (fn [db]
   (:open-managed-funds db)))

(reg-sub
 ::open-securitites
 (fn [db]
   (:open-securities db)))

(reg-sub
 ::company-ids
 (fn [db]
   (:company-ids db)))

(reg-sub
 ::managed-funds-ids
 (fn [db]
   (:managed-funds-ids db)))

;; -- Materialized View -------------------------------------------

(reg-sub
 ::show-back?
 :<- [::active-step]
 (fn [active-step _]
   (> active-step 0)))

(reg-sub
 ::show-print?
 :<- [::active-step]
 (fn [active-step _]
   (= active-step 3)))

(reg-sub
 ::allocation
 :<- [::allocations]
 (fn [allocations [_ id]]
   (get allocations id)))

(reg-sub
 ::allocation-amount
 :<- [::allocations]
 (fn [allocations [_ id]]
   (get-in allocations [id :amount])))

(reg-sub
 ::allocation-percent
 :<- [::allocations]
 :<- [::investment-amount]
 (fn [[allocations total] [_ id]]
   (let [amount (get-in allocations [id :amount])]
     (if (zero? amount)
       0
       (/ (* amount 100) total)))))

(reg-sub
 ::company-total-amount
 :<- [::allocations]
 :<- [::company-ids]
 (fn [[allocations company-ids] _]
   (reduce (fn [total fund]
             (+ total (:amount fund)))
           0
           (vals (select-keys allocations company-ids)))))

(reg-sub
 ::company-total-percent
 :<- [::investment-amount]
 :<- [::company-total-amount]
 (fn [[investment-total company-total] _]
   (investment-percent company-total investment-total)))

(reg-sub
 ::mf-total-amount
 :<- [::allocations]
 :<- [::managed-funds-ids]
 (fn [[allocations mf-ids] _]
   (reduce (fn [total fund]
             (+ total (:amount fund)))
           0
           (vals (select-keys allocations mf-ids)))))

(reg-sub
 ::mf-total-percent
 :<- [::investment-amount]
 :<- [::mf-total-amount]
 (fn [[investment-total mf-total] _]
   (investment-percent mf-total investment-total)))

(reg-sub
 ::unallocated-amount
 :<- [::investment-amount]
 :<- [::allocations]
 (fn [[total allocations] _]
   (- total (reduce (fn [acc allocation]
                      (+ acc (:amount allocation)))
                    0
                    (vals allocations)))))

(reg-sub
 ::unallocated-percent
 :<- [::investment-amount]
 :<- [::unallocated-amount]
 (fn [[total amount] _]
   (investment-percent amount total)))

(reg-sub
 ::calculator
 :<- [::product]
 (fn [product _]
   (case product
     :product-a a-fees-calculator
     :product-b b-fees-calculator
     :product-c b-fees-calculator)))

(reg-sub
 ::fees
 :<- [::allocations]
 :<- [::calculator]
 (fn [[allocations calculator] _]
   (calculator (vals allocations))))

(defn fee-amount [fee-type]
  (fn [fees _]
    (.parseFloat js/Number (get fees fee-type))))

(defn fee-percent [[amount total] _]
  (.toFixed (investment-percent amount total) 2))

(reg-sub
 ::min-fee-amount
 :<- [::investment-amount]
 :<- [::calculator]
 (fn [[total calculator] _]
   (.parseFloat js/Number (:total (calculator [{:type :cash-hub
                                                :amount 2500}
                                               {:type :company-fund
                                                :amount (- total 2500)}])))))

(reg-sub
 ::min-fee-percent
 :<- [::min-fee-amount]
 :<- [::investment-amount]
 fee-percent)

(reg-sub
 ::max-fee-amount
 :<- [::investment-amount]
 :<- [::calculator]
 (fn [[total calculator] _]
   (.parseFloat js/Number (:total (calculator [{:type :cash-hub
                                                :amount total}])))))

(reg-sub
 ::max-fee-percent
 :<- [::max-fee-amount]
 :<- [::investment-amount]
 fee-percent)

(reg-sub
 ::asset-based-fee-amount
 :<- [::fees]
 (fn [fees _]
   (add-string-numbers (:admin fees) (:promoter fees))))

(reg-sub
 ::asset-based-fee-percent
 :<- [::asset-based-fee-amount]
 :<- [::investment-amount]
 fee-percent)

(reg-sub
 ::account-keeping-fee-amount
 :<- [::fees]
 (fee-amount :account))

(reg-sub
 ::account-keeping-fee-percent
 :<- [::account-keeping-fee-amount]
 :<- [::investment-amount]
 fee-percent)

(reg-sub
 ::expense-recovery-fee-amount
 :<- [::fees]
 (fee-amount :orfr))

(reg-sub
 ::expense-recovery-fee-percent
 :<- [::expense-recovery-fee-amount]
 :<- [::investment-amount]
 fee-percent)

(reg-sub
 ::administration-fee-amount
 :<- [::fees]
 (fee-amount :total))

(reg-sub
 ::administration-fee-percent
 :<- [::administration-fee-amount]
 :<- [::investment-amount]
 fee-percent)

(reg-sub
 ::filtered-securities
 :<- [::product]
 :<- [::securites]
 :<- [::search-string]
 :<- [::admin-fee-option]
 :<- [::asset-class-option]
 (fn [[product securities string fee-options class-options] _]
   (filter (fn [security]
             (and (get security product)
                  (if (not-empty fee-options)
                    (some #(= (:category security) (:category %)) fee-options)
                    true)
                  (if (not-empty class-options)
                    (some #{(:classification security)} class-options)
                    true)
                  (includes? (:issuer security) string)))
           (sort-by :issuer (vals securities)))))

(reg-sub
 ::asset-classes
 :<- [::securites]
 (fn [securities _]
   (reduce
    (fn [acc fund]
      (if (some? (:classification fund))
        (conj acc (:classification fund))
        acc))
    #{}
    (vals securities))))

(reg-sub
 ::product-formatted
 :<- [::product]
 (fn [product _]
   (condp = product
     :product-a "Product A"
     :product-b "Product B"
     :product-c "Product C")))

(reg-sub
 ::investment-amount-formatted
 :<- [::investment-amount]
 (fn [amount _]
   (number->currency amount)))

(reg-sub
 ::show-add-company-funds?
 :<- [::company-ids]
 (fn [company-ids _]
   (< (count company-ids) 4)))

(reg-sub
 ::portfolio-high
 :<- [::investment-amount]
 :<- [::allocations]
 (fn [[total allocations] _]
   (investment-percent (reduce (fn [acc allocation]
                                 (if (not= :company-fund (:type allocation))
                                   (+ acc (:amount allocation))
                                   acc))
                               0
                               (vals allocations))
                       total)))

(reg-sub
 ::portfolio-low
 :<- [::investment-amount]
 :<- [::allocations]
 (fn [[total allocations] _]
   (investment-percent (reduce (fn [acc allocation]
                                 (if (= :company-fund (:type allocation))
                                   (+ acc (:amount allocation))
                                   acc))
                               0
                               (vals allocations))
                       total)))

(reg-sub
 ::fee-orfr
 :<- [::fees]
 (fn [{:keys [orfr total]} _]
   (investment-percent (.parseFloat js/Number orfr) (.parseFloat js/Number total))))

(reg-sub
 ::fee-high
 :<- [::fees]
 (fn [{:keys [total breakdown]} _]
   (investment-percent (reduce (fn [acc [_ {:keys [fees]}]]
                                 (+ acc (.parseFloat js/Number (:total fees))))
                               0
                               (filter #(not= :company-fund (first %))
                                       breakdown))
                       (.parseFloat js/Number total))))

(reg-sub
 ::fee-low
 :<- [::fees]
 (fn [{:keys [total breakdown]} _]
   (investment-percent (get-in breakdown [:company-fund :fees :total])
                       total)))

(reg-sub
 ::mappings
 :<- [::portfolio-high]
 :<- [::portfolio-low]
 :<- [::fee-high]
 :<- [::fee-low]
 (fn [[portfolio-high portfolio-low fee-high fee-low] _]
   [{:id "0"
     :from 0
     :to 0}
    {:id "low"
     :from portfolio-low
     :to fee-low}
    {:id "high"
     :from (+ portfolio-high portfolio-low)
     :to (+ fee-low fee-high)}]))
