<template>
  <div id="app">
    <Toolbar class="p-mb-3">
      <template #left>
        <h2 class="p-m-0">Phone Book</h2>
      </template>

      <template #right>
        <a href="mailto:jean1995sib@gmail.com?subject=subject"><i class="pi pi-envelope"></i></a>
        <a href="https://github.com/JeanPonomarev/phone-book-springboot" target="_blank"><i class="pi pi-github p-ml-3"></i></a>
      </template>
    </Toolbar>

    <PhoneBook/>
  </div>
</template>

<script>

import PhoneBook from "./components/PhoneBook";

export default {
  name: 'App',
  components: {
    PhoneBook
  }
}
</script>

<style scoped>
  #app {
    margin: 0 auto;
    max-width: 80%;
  }

  i.pi {
    font-size: 2rem;
  }
</style>