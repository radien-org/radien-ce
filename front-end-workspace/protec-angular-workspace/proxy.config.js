const env = {
  online: 'https://test.protec.help',
  local: 'http://localhost:8090'
};
const proxy = [
    {
      context: "/api", 
      "target": env.online + "/nwprotecservice/v1/", 
      "secure": false, 
      "pathRewrite": {
      "^/api": ""
      },
      "logLevel": "debug"
      }
  ];
  module.exports = proxy;
  