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
    },
    {
      context: "/cms", 
      "target": env.online + "/cms/v1/ ", 
      "secure": false, 
      "pathRewrite": {
      "^/cms": ""
      },
      "logLevel": "debug"
    }
  ];
  module.exports = proxy;
  