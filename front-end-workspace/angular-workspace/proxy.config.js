const proxy = [
    {
      context: '/api',
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  ];
  module.exports = proxy;
  