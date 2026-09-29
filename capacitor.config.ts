import type { CapacitorConfig } from "@capacitor/cli";

const config: CapacitorConfig = {
  appId: "com.antonservice.pos",
  appName: "Anton Service POS",
  webDir: "dist",
  bundledWebRuntime: false,

  android: {
    allowMixedContent: true
  },

  server: {
    androidScheme: "https"
  }
};

export default config;
