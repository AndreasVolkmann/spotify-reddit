<!DOCTYPE html>
<html>
<head>
    <title>Spotify Reddit Dynamic Playlist</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.1/css/bulma.min.css">
    <script defer src="https://use.fontawesome.com/releases/v5.0.7/js/all.js"></script>
</head>

<section class="hero is-primary is-medium is-bold is-fullheight">

    <div class="hero-head">
        <nav class="navbar">
            <div class="container">
                <div class="navbar-brand">
                    <a class="navbar-item">
                        <div class="fa-2x">
                            <#include "logo.ftl">
                        </div>
                        <span style="padding-left: 5px">Spottit</span>
                        <!--<img src="https://bulma.io/images/bulma-type-white.png" alt="Logo"> -->
                    </a>
                    <span class="navbar-burger burger" data-target="navbarMenuHeroA">
            <span></span>
            <span></span>
            <span></span>
          </span>
                </div>
                <div id="navbarMenuHeroA" class="navbar-menu" style="padding-top: 15px">
                    <div class="navbar-end">
                        <!--
                        <a class="navbar-item is-active">
                            Home
                        </a>
                        <a class="navbar-item">
                            Examples
                        </a>
                        <a class="navbar-item">
                            Documentation
                        </a>
                        -->
                        <span class="navbar-item">
                            <a class="button is-primary is-inverted"
                               href="https://github.com/AndreasVolkmann/spotify-reddit">
                                <span class="icon">
                                <i class="fab fa-github"></i>
                                </span>
                                <span>Source</span>
                            </a>
                        </span>
                    </div>
                </div>
            </div>
        </nav>
</div>