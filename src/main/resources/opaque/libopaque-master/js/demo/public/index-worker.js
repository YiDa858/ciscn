(function (root) {
  "use strict";

  var infos;
  var cfg;
  var idS = "server";

  function registerWithPassword(idU, module, pwdU) {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", "/register-with-password", true);
    xhr.onreadystatechange = function () {
      var response = onreadystatechange(module, xhr);
      if (response) requestCredentials(idU, module, pwdU);
    };
    xhrSend(
      "id=" + encodeURIComponent(idU) + "&pw=" + encodeURIComponent(pwdU),
      module,
      xhr
    );
  }

  function requestCredentials(idU, module, pw) {
    try {
      var request = module.createCredentialRequest({ pwdU: pw });
      var pub_base16 = module.uint8ArrayToHex(request.pub);
      var xhr = new XMLHttpRequest();
      xhr.open("POST", "/request-credentials", true);
      xhr.onreadystatechange = function () {
        var response = onreadystatechange(module, xhr);
        if (response) recoverCredentials(idU, module, response, request);
      };
      xhrSend(
        "id=" + encodeURIComponent(idU) + "&request=" + pub_base16,
        module,
        xhr
      );
    } catch (e) {
      module.printErr(e);
    }
  }

  function recoverCredentials(idU, module, response, request) {
    try {
      var resp_base16 = response.response;
      var pkS_base16 = response.pkS;
      var credentials = module.recoverCredentials({
        resp: module.hexToUint8Array(resp_base16),
        sec: request.sec,
        pkS: pkS_base16 ? module.hexToUint8Array(pkS_base16) : null,
        cfg: cfg,
        infos: infos,
        ids: { idS: idS, idU: idU },
      });
      var authU_base16 = module.uint8ArrayToHex(credentials.authU);
      var xhr = new XMLHttpRequest();
      xhr.open("POST", "/authorize", true);
      xhr.onreadystatechange = function () {
        var response = onreadystatechange(module, xhr);
        if (response != null)
          module.print(
            response === true ? "Success!" : "You are not authorized!"
          );
      };
      xhrSend(
        "auth=" +
          encodeURIComponent(authU_base16) +
          "&id=" +
          encodeURIComponent(idU),
        module,
        xhr
      );
    } catch (e) {
      console.log(e);
      module.printErr(e);
    }
  }

  function registerWithoutPassword(idU, module, pwdU) {
    try {
      var request = module.createRegistrationRequest({ pwdU: pwdU });
      var M_base16 = module.uint8ArrayToHex(request.M);
      var xhr = new XMLHttpRequest();
      xhr.open("POST", "/register-without-password", true);
      xhr.onreadystatechange = function () {
        var response = onreadystatechange(module, xhr);
        if (response) finalizeRequest(idU, module, request, response, pwdU);
      };
      xhrSend(
        "request=" + encodeURIComponent(M_base16) + "&id=" + idU,
        module,
        xhr
      );
    } catch (e) {
      module.printErr(e);
    }
  }

  function registerWithGlobalServerKey(idU, module, pwdU) {
    try {
      var request = module.createRegistrationRequest({ pwdU: pwdU });
      var M_base16 = module.uint8ArrayToHex(request.M);
      var xhr = new XMLHttpRequest();
      xhr.open("POST", "/register-with-global-server-key", true);
      xhr.onreadystatechange = function () {
        var response = onreadystatechange(module, xhr);
        if (response) finalizeRequest(idU, module, request, response, pwdU);
      };
      xhrSend(
        "request=" + encodeURIComponent(M_base16) + "&id=" + idU,
        module,
        xhr
      );
    } catch (e) {
      module.printErr(e);
    }
  }

  function finalizeRequest(idU, module, request, response, pwdU) {
    try {
      var pub_base16 = response.response;
      var result = module.finalizeRequest({
        sec: request.sec,
        pub: module.hexToUint8Array(pub_base16),
        cfg: cfg,
        ids: { idS: idS, idU: idU },
      });
      var rec_base16 = module.uint8ArrayToHex(result.rec);
      var xhr = new XMLHttpRequest();
      xhr.open(
        "POST",
        response.type === "global-server-key"
          ? "/store-user-record-using-global-server-key"
          : "/store-user-record",
        true
      );
      xhr.onreadystatechange = function () {
        var response = onreadystatechange(module, xhr);
        if (response != null && response === true)
          requestCredentials(idU, module, pwdU);
      };
      xhrSend(
        "rec=" + encodeURIComponent(rec_base16) + "&id=" + idU,
        module,
        xhr
      );
    } catch (e) {
      console.log(e);
      module.printErr(e);
    }
  }

  function onreadystatechange(module, xhr) {
    try {
      if (xhr.readyState !== XMLHttpRequest.DONE) return;
      if (xhr.status !== 200 || !xhr.response) {
        module.printErr(xhr.responseURL + " failed.");
        return;
      }
      module.print(xhr.responseURL);
      module.print("FROM SERVER: " + xhr.response);
      var json = JSON.parse(xhr.response);
      if (!json) {
        module.printErr(xhr.responseURL + " failed.");
        return;
      }
      if (json.error) {
        module.printErr(json.error);
        return;
      }
      return json;
    } catch (e) {
      module.printErr(e);
    }
  }

  function xhrSend(body, module, xhr) {
    module.print("TO SERVER: " + body);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhr.send(body);
  }

  // Module is boilerplate generated by Emscripten. We have modified it some.
  var Module = {
    preRun: [],
    postRun: [],
    print: function (text) {
      if (arguments.length > 1)
        text = Array.prototype.slice.call(arguments).join(" ");
      if (typeof text === "string" || text instanceof String)
        postMessage({ print: text });
      else if (text instanceof Error) postMessage({ print: text.message });
      else postMessage({ print: JSON.stringify(text) });
    },
    printErr: function (text) {
      if (arguments.length > 1)
        text = Array.prototype.slice.call(arguments).join(" ");
      if (typeof text === "string" || text instanceof String)
        postMessage({ printErr: text });
      else if (text instanceof Error) postMessage({ printErr: text.message });
      else postMessage({ printErr: JSON.stringify(text) });
    },
    setStatus: function (text) {
      if (!Module.setStatus.last)
        Module.setStatus.last = { time: Date.now(), text: "" };
      if (text === Module.setStatus.last.text) return;
      var m = text.match(/([^(]+)\((\d+(\.\d+)?)\/(\d+)\)/);
      var now = Date.now();
      if (m && now - Module.setStatus.last.time < 30) return; // if this is a progress update, skip it if too soon
      Module.setStatus.last.time = now;
      Module.setStatus.last.text = text;
      // Send a message to index.js.
      // https://developer.mozilla.org/en-US/docs/Web/API/Worker/postmessage
      postMessage({ print: text });
    },
    totalDependencies: 0,
    monitorRunDependencies: function (left) {
      this.totalDependencies = Math.max(this.totalDependencies, left);
      Module.setStatus(
        left
          ? "Preparing... (" +
              (this.totalDependencies - left) +
              "/" +
              this.totalDependencies +
              ")"
          : "All downloads complete."
      );
    },
  };
  Module.setStatus("Downloading...");
  root.onerror = function (event) {
    // TODO: do not warn on ok events like simulating an infinite loop or exitStatus
    Module.setStatus("Exception thrown, see JavaScript console");
    Module.setStatus = function (text) {
      if (text) Module.printErr("[post-exception status] " + text);
    };
  };

  // See the end of libopaque-post.js for where we hook up root.libopaque_mod.
  root.libopaque_mod = Module;

  // https://developer.mozilla.org/en-US/docs/Web/API/WorkerGlobalScope/importScripts
  root.importScripts("libopaque.js");

  // Receive a message from index.js.
  // https://developer.mozilla.org/en-US/docs/Web/API/Worker/onmessage
  root.onmessage = function (e) {
    var action = e.data.action;
    var idU = e.data.id;
    var pwdU = e.data.pw;
    if (action === "register-with-password") {
      registerWithPassword(idU, Module, pwdU);
    } else if (action === "register-without-password") {
      registerWithoutPassword(idU, Module, pwdU);
    } else if (action === "register-with-global-server-key") {
      registerWithGlobalServerKey(idU, Module, pwdU);
    } else if (action === "login") {
      requestCredentials(idU, Module, pwdU);
    } else {
      Module.printErr(action + " is invalid.");
    }
  };
})(this);
