import $ from 'jquery';
import './graphical-editor/graphical-editor';
import './graphical-editor/model/ext/LinkView';

import '../assets/icon-to-back.svg';
import '../assets/icon-to-front.svg';

import './graphical-editor/model/css/halo.css';
import { MLink, MLinkView } from './graphical-editor/model/ext/zeta.link';
import './graphical-editor/model/ext/modelValidator';

import joint from 'jointjs';
import Backbone from 'backbone1.0';
import chat from './graphical-editor/model/ext/chat';
import { CommonInspectorInputs, CommonInspectorGroups, inp } from './graphical-editor/model/inspector';
import Stencil from './graphical-editor/model/stencil';
import Main from './graphical-editor/model';

$(document).ready(function() {
    joint.shapes.zeta.MLink = MLink;
    joint.shapes.zeta.MLinkView = MLinkView;
    new Main();
    Backbone.history.start();
});

global.joint = joint;
global.chat = chat;
global.CommonInspectorInputs = CommonInspectorInputs;
global.CommonInspectorGroups = CommonInspectorGroups;
global.inp = inp;
global.Stencil = Stencil;
