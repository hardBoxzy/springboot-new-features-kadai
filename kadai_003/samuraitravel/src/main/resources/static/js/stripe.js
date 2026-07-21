const stripe = Stripe('pk_test_51Ttnh2IL83NwYNRHJeU8YmCBm3LDwBoDvVCtXOCbpNKStPh7YP3YuKPZPPBg9GbuUIBAyqpT0rdq2MphDpHpX9R000VebZINyo');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
 stripe.redirectToCheckout({
   sessionId: sessionId
 })
});