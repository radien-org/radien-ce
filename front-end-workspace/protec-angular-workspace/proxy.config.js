const proxy = [
    {
      context: "/api/cookie",
      "target": "http://localhost:8090/nwprotecservice/v1/cookie",  
      "secure": false,
      "pathRewrite": {
      "^/api/cookie": ""
      },
      "logLevel": "debug"
      }
  ];
  module.exports = proxy;
  