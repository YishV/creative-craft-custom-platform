<template>
  <div v-loading="true" class="role-redirect" element-loading-text="正在跳转..."></div>
</template>

<script>
import { getAuthType } from '@/utils/auth'

export default {
  name: "Index",
  created() {
    document.title = "正在跳转..."
    this.routeByRole()
  },
  methods: {
    routeByRole() {
      const roles = (this.$store.getters && this.$store.getters.roles) || []
      let target = "/portal/products"
      if (getAuthType() === 'creator') {
        target = "/creative/creator/me"
      } else if (roles.includes("admin")) {
        target = "/creative-dashboard"
      } else if (roles.includes("creator")) {
        target = "/creative/creator/me"
      } else if (roles.includes("buyer")) {
        target = "/portal/products"
      }
      this.$router.replace(target).catch(() => {})
    }
  }
}
</script>

<style scoped>
.role-redirect {
  height: calc(100vh - 84px);
}
</style>
