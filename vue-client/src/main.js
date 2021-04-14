import Vue from 'vue'
import App from './App.vue'

import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Toolbar from 'primevue/toolbar';
import Dialog from 'primevue/dialog';
import InputText from 'primevue/inputtext';
import Button from 'primevue/button';
import ToastService from 'primevue/toastservice';
import Toast from 'primevue/toast';
import Panel from 'primevue/panel';

import 'primevue/resources/themes/nova/theme.css';
import 'primevue/resources/primevue.min.css';
import 'primeicons/primeicons.css';

import 'primeflex/primeflex.css';

// import 'primeflex/src/_variables.scss';
// import 'primeflex/src/_grid.scss';
// import 'primeflex/src/_formlayout.scss';
// import 'primeflex/src/_display.scss';
// import 'primeflex/src/_text.scss';
// import 'primeflex/src/flexbox/_flexbox.scss';
// import 'primeflex/src/_spacing.scss';
// import 'primeflex/src/_elevation.scss';

Vue.config.productionTip = false

Vue.component('DataTable', DataTable);
Vue.component('Column', Column);
Vue.component('Toolbar', Toolbar);
Vue.component('Dialog', Dialog);
Vue.component('InputText', InputText);
Vue.component('Button', Button);
Vue.component('Toast', Toast);
Vue.component('Panel', Panel);

Vue.use(ToastService);

new Vue({
  render: h => h(App),
}).$mount('#app')
